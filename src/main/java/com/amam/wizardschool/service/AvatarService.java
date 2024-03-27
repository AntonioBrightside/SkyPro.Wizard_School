package com.amam.wizardschool.service;

import com.amam.wizardschool.dto.AvatarDto;
import com.amam.wizardschool.exception.AvatarNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.mapper.AvatarMapper;
import com.amam.wizardschool.model.Avatar;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.AvatarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private final StudentService studentService;
    private final AvatarRepository avatarRepository;
    public final AvatarMapper avatarMapper;

    @Value("${path.to.avatars.dir}")
    private String avatarsDir;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository, AvatarMapper avatarMapper) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
        this.avatarMapper = avatarMapper;
    }

    public void uploadAvatar(Long id, MultipartFile file) throws IOException, StudentNotFoundException {
        Student student = studentService.findStudent(id)
                .orElseThrow(() -> new StudentNotFoundException("Student was not found"));
        Path filePath = Path.of(avatarsDir, student.getId() + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());

        avatarRepository.findById(id).ifPresent(o -> {
            try {
                Files.deleteIfExists(Path.of(o.getFilePath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

//        Files.deleteIfExists(filePath);

        try (
                InputStream is = file.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = findAvatarOrCreateNewOne(id);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setSmallPhoto(file.getBytes()); // TODO: подгружает некорректную картинку. Разобраться при использовании  generateSmallPhoto()
        avatarRepository.save(avatar);
    }

    public Avatar findAvatarOrCreateNewOne(Long id) {
        return avatarRepository.findById(id).orElse(new Avatar());
    }

    public Avatar findAvatar(Long id) throws AvatarNotFoundException {
        return avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException("Avatar was not found"));
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generateSmallPhoto(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            BufferedImage image = ImageIO.read(bis);
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage smallPhoto = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = smallPhoto.createGraphics();
            graphics2D.drawImage(smallPhoto, 0, 0, 100, height, null);
            graphics2D.dispose();

            ImageIO.write(smallPhoto, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();

        }
    }

    public Collection<AvatarDto> getAllAvatars(int pages, int size) {
        return avatarRepository.findAll(PageRequest.of(pages - 1, size))
                .get()
                .map(avatarMapper::toDto)
                .collect(Collectors.toList());
    }
}
