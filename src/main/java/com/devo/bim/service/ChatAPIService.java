package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.ChattingAlertDTO;
import com.devo.bim.model.dto.RocketChatAPIResponse;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Chatting;
import com.devo.bim.model.entity.ChattingJoiner;
import com.devo.bim.model.entity.CoWork;
import com.devo.bim.model.vo.AccountVO;
import com.devo.bim.repository.dsl.CoWorkDslRepository;
import com.devo.bim.repository.spring.AccountRepository;
import com.devo.bim.repository.spring.ChattingJoinerRepository;
import com.devo.bim.repository.spring.ChattingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAPIService extends AbstractService {

    @Value("${system.chatting_server.host}")
    private String chatServerHost;
    @Value("${system.chatting_server.port}")
    private String chatServerPort;
    @Value("${system.chatting_server.admin_id}")
    private String chatServerAdminId;
    @Value("${system.chatting_server.admin_token}")
    private String chatServerAdminToken;
    @Value("${system.server_domain}")
    private String baseDomain;

    private final AccountRepository accountRepository;
    private final ChattingRepository chattingRepository;
    private final ChattingJoinerRepository chattingJoinerRepository;
    private final CoWorkDslRepository coWorkDslRepository;

    private final WebClient webClient = WebClient.create();

    @Transactional
    public boolean procAuth(UserInfo userInfo) {
        try {
            String savedToken = userInfo.getRocketChatToken();
            if (StringUtils.isBlank(savedToken)) {
                updateUserToken(userInfo);
                return true;
            }

            RocketChatAPIResponse loginResponse = tryLogin(userInfo.getRocketChatToken());
            if (loginResponse.getData() == null) {
                updateUserToken(userInfo);
                loginResponse = tryLogin(userInfo.getRocketChatToken());
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void updateUserToken(UserInfo userInfo) {
        String refreshToken = createUserToken(userInfo).getData().getAuthToken();
        Account account = accountRepository.findById(userInfo.getId()).orElseGet(Account::new);
        account.setRocketChatToken(refreshToken);
        userInfo.setRocketChatToken(refreshToken);
    }

    public RocketChatAPIResponse tryLogin(String userToken) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("login"))
                .build()
                .post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData("resume", userToken))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> response.bodyToMono(RocketChatAPIResponse.class).flatMap(body -> {
                    if ((int) body.getError() == 403) {
                        return Mono.empty();
                    }
                    log.error("/api/v1/login Error[" + response.rawStatusCode() + "]: " + body);
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                }))
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    public RocketChatAPIResponse createUser(AccountVO accountVO, String companyName) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("users.create"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", chatServerAdminId);
                    headers.add("X-Auth-Token", chatServerAdminToken);
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(getCreateUserParams(accountVO, companyName)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/users.create Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    @NotNull
    private MultiValueMap<String, String> getCreateUserParams(AccountVO accountVO, String companyName) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", accountVO.getEmail());
        params.add("name", accountVO.getUserName());
        params.add("password", accountVO.getPassword());
        params.add("username", getCompanyNameWithUserName(companyName, accountVO.getUserName()));
        return params;
    }

    private String getCompanyNameWithUserName(String companyName, String userName) {
        return "[" + companyName + "]" + "_" + userName;
    }

    public RocketChatAPIResponse updateUser(AccountVO accountVO, String rocketChatId) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("users.update"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", chatServerAdminId);
                    headers.add("X-Auth-Token", chatServerAdminToken);
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(getUpdateUserParams(accountVO, rocketChatId)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/users.update Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    private MultiValueMap<String, String> getUpdateUserParams(AccountVO accountVO, String rocketChatId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", rocketChatId);
        params.add("data[name]", accountVO.getUserName());
        params.add("data[password]", accountVO.getPassword());
        params.add("data[username]", accountVO.getUserName());
        return params;
    }

    public RocketChatAPIResponse setUserAvatar(Account account) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("users.setAvatar"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", chatServerAdminId);
                    headers.add("X-Auth-Token", chatServerAdminToken);
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(getUserAvatarParams(account)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/users.setAvatar Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    @NotNull
    private MultiValueMap<String, String> getUserAvatarParams(Account account) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", account.getRocketChatId());
        params.add("avatarUrl", baseDomain + account.getPhotoPath());
        return params;
    }

    public RocketChatAPIResponse createUserToken(UserInfo userInfo) {

        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("users.createToken"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", chatServerAdminId);
                    headers.add("X-Auth-Token", chatServerAdminToken);
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData("userId", userInfo.getRocketChatId()))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/users.createToken Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    private String getChatServerEndPoint(String path) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(chatServerHost)
                .port(chatServerPort)
                .path("/api/v1/{path}")
                .buildAndExpand(path)
                .toUriString();
    }

    @Transactional
    public boolean procCreateTeam(CoWork coWork) {
        String teamName = getCreateTeamName(coWork.getId());

        try {
            RocketChatAPIResponse createTeamResponse = createTeam(teamName);
            if (!createTeamResponse.isSuccess()) {
                return false;
            }
            Chatting newChatting = postChat(coWork, teamName, createTeamResponse.getTeam().getRoomId());
            postChatJoiner(newChatting, userInfo.getId(), true);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void postChatJoinerAtPutWork(Chatting chatting, List<Long> joinerIds) {
        String[] rocketIds = accountRepository.findAllById(joinerIds).stream().filter(t -> t.getId() != userInfo.getId()).map(Account::getRocketChatId).toArray(String[]::new);
        RocketChatAPIResponse rocketChatAPIResponse = addMembersToTeam(chatting.getTeamName(), rocketIds);

        if (rocketChatAPIResponse.isSuccess()) {
            List<ChattingJoiner> chattingJoinerList = chatting.getChattingJoiners();

            // 기존 채팅 참여자는 insert 대상 제외
            joinerIds = joinerIds.stream().filter(t -> chattingJoinerList.stream().noneMatch(c -> c.getJoiner().getId() == t)).collect(Collectors.toList());

            for (Long joinerId : joinerIds) {
                postChatJoiner(chatting, joinerId, false);
            }
        }
    }

    private void postChatJoiner(Chatting chatting, long joinerId, boolean isOwner) {
        ChattingJoiner chattingJoiner = new ChattingJoiner();
        chattingJoiner.setChattingJoiner(chatting, joinerId, isOwner);
        chattingJoinerRepository.save(chattingJoiner);
    }

    private Chatting postChat(CoWork coWork, String teamName, String roomId) {
        Chatting chatting = new Chatting();
        chatting.setChatting(coWork, teamName, userInfo, roomId);
        return chattingRepository.save(chatting);
    }

    public void deleteChatJoinerAtPutWork(Chatting chatting, long joinerId) {
        Account account = accountRepository.findById(joinerId).orElseGet(Account::new);
        if (account.getId() != 0) {
            RocketChatAPIResponse rocketChatAPIResponse = removeMemberFromTeam(chatting.getTeamName(), account.getRocketChatId());
            if (rocketChatAPIResponse.isSuccess()) {
                deleteChatJoiner(chatting.getId(), joinerId);
            }
        }
    }

    private void deleteChatJoiner(long chatId, long joinerId) {
        ChattingJoiner chattingJoiner = chattingJoinerRepository.findByChattingIdAndJoinerId(chatId, joinerId).orElseGet(ChattingJoiner::new);

        if (chattingJoiner.getId() != 0) {
            chattingJoinerRepository.delete(chattingJoiner);
        }
    }

    private String getCreateTeamName(long coWorkId) {
        return "cowk_" + userInfo.getProjectId() + "_" + coWorkId;
    }

    private RocketChatAPIResponse createTeam(String teamName) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("teams.create"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", userInfo.getRocketChatId());
                    headers.add("X-Auth-Token", userInfo.getRocketChatToken());
                })
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getCreateTeamParams(teamName)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/teams.create Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    private String getCreateTeamParams(String teamName) {
        return "{\n" +
                "    \"name\": \"" + teamName + "\",\n" +
                "    \"type\": 1,\n" +
                "    \"members\": [\n" +
                "        \"" + userInfo.getRocketChatId() + "\"\n" +
                "    ]\n" +
                "}";
    }

    private RocketChatAPIResponse addMembersToTeam(String teamName, String[] memberIds) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("teams.addMembers"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", userInfo.getRocketChatId());
                    headers.add("X-Auth-Token", userInfo.getRocketChatToken());
                })
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(getAddMembersToTeamParams(teamName, memberIds)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/teams.addMembers Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    @NotNull
    private MultiValueMap<String, String> getAddMembersToTeamParams(String teamName, String[] memberIds) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("teamName", teamName);

        for (int i = 0; i < memberIds.length; i++) {
            String key = "members" + "[" + i + "]" + "[userId]";
            params.add(key, memberIds[i]);
        }
        return params;
    }

    private RocketChatAPIResponse removeMemberFromTeam(String teamName, String userId) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("teams.removeMember"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", userInfo.getRocketChatId());
                    headers.add("X-Auth-Token", userInfo.getRocketChatToken());
                })
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(getRemoveMemberFromTeamParams(teamName, userId)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/teams.removeMember Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    @NotNull
    private MultiValueMap<String, String> getRemoveMemberFromTeamParams(String teamName, String userId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("teamName", teamName);
        params.add("userId", userId);
        return params;
    }

    public boolean setReadOnlyChatting(String teamName) {
        try {
            RocketChatAPIResponse response = groupsReadOnly(teamName);
            return response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    private RocketChatAPIResponse groupsReadOnly(String teamName) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("groups.setReadOnly"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", userInfo.getRocketChatId());
                    headers.add("X-Auth-Token", userInfo.getRocketChatToken());
                })
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getGroupsReadOnlyParams(teamName)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/groups.setReadOnly Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    private String getGroupsReadOnlyParams(String teamName) {
        return "{\n" +
                "    \"roomName\": \"" + teamName + "\",\n" +
                "    \"readOnly\": true \n" +
                "}";
    }

    public List<ChattingAlertDTO> getUserChatInfo() {

        List<ChattingAlertDTO> chattingAlertDTOs = coWorkDslRepository.findUserJoinedCoWorks(userInfo.getProjectId(), userInfo.getId());

        RocketChatAPIResponse rocketChatAPIResponse = getUsersInfo(userInfo.getRocketChatId());

        List<RocketChatAPIResponse.Room> roomList;
        if (rocketChatAPIResponse.isSuccess()) {
            roomList = rocketChatAPIResponse.getUser().getRooms();
        } else {
            return new ArrayList<>();
        }

        for (ChattingAlertDTO chattingAlertDTO : chattingAlertDTOs) {
            chattingAlertDTO.setUnread(roomList.stream().filter(t -> t.getRid().equals(chattingAlertDTO.getRoomId())).findFirst().orElseGet(RocketChatAPIResponse.Room::new).getUnread());
        }

        return chattingAlertDTOs.stream().filter(t -> t.getUnread() > 0).sorted(Comparator.comparing(ChattingAlertDTO::getCoWorkId).reversed()).collect(Collectors.toList());
    }

    private RocketChatAPIResponse getUsersInfo(String userId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("fields", "{\"userRooms\": 1}");

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(getChatServerEndPoint("users.info"))
                .queryParams(params)
                .build();

        return webClient.mutate()
                .build()
                .get()
                .uri(uri.toUri())
                .headers(headers -> {
                    headers.add("X-User-Id", chatServerAdminId);
                    headers.add("X-Auth-Token", chatServerAdminToken);
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    response.bodyToMono(String.class).publishOn(Schedulers.boundedElastic()).subscribe(body -> {
                        log.error("/api/v1/users.info Error[" + response.rawStatusCode() + "]: " + body);
                    });
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                })
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }

    public void tryLogout(String accountEmail) {

        Account account = accountRepository.findByEmail(accountEmail).orElseGet(Account::new);

        if (account.getId() != 0) {
            RocketChatAPIResponse response = logout(account.getRocketChatId(), account.getRocketChatToken());
        }
    }

    private RocketChatAPIResponse logout(String userId, String userToken) {
        return webClient.mutate()
                .baseUrl(getChatServerEndPoint("logout"))
                .build()
                .post()
                .headers(headers -> {
                    headers.add("X-User-Id", userId);
                    headers.add("X-Auth-Token", userToken);
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> response.bodyToMono(RocketChatAPIResponse.class).flatMap(body -> {
                    if (body.getError () == null || ((int) body.getError() == 403)) {
                        return Mono.empty();
                    }
                    log.error("/api/v1/logout Error[" + response.rawStatusCode() + "]: " + body);
                    return Mono.error(new ServiceException("Error " + response.rawStatusCode()));
                }))
                .bodyToMono(RocketChatAPIResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElse(new RocketChatAPIResponse());
    }
}
