package com.devo.bim.service;

import com.devo.bim.model.entity.Menu;
import com.devo.bim.repository.spring.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Cacheable(value = "menus")
    public List<Menu> menuAll(){
        return menuRepository.findAll();
    }
}
