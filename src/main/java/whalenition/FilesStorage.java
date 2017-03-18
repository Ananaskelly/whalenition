package whalenition;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FilesStorage {

    private final Path rootLocation;

    @Autowired
    public FilesStorage() {
        this.rootLocation = Paths.get("upload");
    }

    public void store(MultipartFile file) {
        try {
            String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), "jpg");
            Files.copy(file.getInputStream(), this.rootLocation.resolve(name));
        } catch (IOException e) {
            //
        }
    }

    public Optional<Path> getLast() {
        try {
            Optional<Path> lastFilePath = Files.list(this.rootLocation)
                    .filter(f -> !Files.isDirectory(f))
                    .max((f1, f2) -> (int)(f1.toFile().lastModified() - f2.toFile().lastModified()));
            if (lastFilePath.isPresent())
            {
                return lastFilePath;
            }
        } catch (IOException e) {
            //
        }
        return null;
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
           //
        }
    }
}