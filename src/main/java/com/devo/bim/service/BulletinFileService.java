package com.devo.bim.service;

import com.devo.bim.model.entity.BulletinFile;
import com.devo.bim.repository.spring.BulletinFileRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BulletinFileService extends AbstractService {
    private final BulletinFileRepository bulletinFileRepository;
    private final FileDeleteService fileDeleteService;

    @Transactional
    public JsonObject deleteBulletinFile(long id) {
        BulletinFile savedBulletinFile = bulletinFileRepository.findById(id).orElseGet(BulletinFile::new);
        if (savedBulletinFile.getId() == 0) return proc.getResult(false, "system.bulletin_file_service.not_exist_bulletin_file");
        try {
            fileDeleteService.deletePhysicalFile(savedBulletinFile.getFilePath());
            bulletinFileRepository.delete(savedBulletinFile);

            return proc.getResult(true, "system.bulletin_file_service.delete_bulletin_file");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
