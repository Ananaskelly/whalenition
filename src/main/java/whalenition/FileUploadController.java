package whalenition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import recognition.Recognizer;
import recognition.RecognizerAnswer;

@Controller
public class FileUploadController {

    private final FilesStorage storageService;

    @Autowired
    public FileUploadController(FilesStorage storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/recognize", produces = "application/json")
    public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
        RecognizerAnswer answ = Recognizer.run(storageService);
        storageService.deleteAll();
        return "" + answ.mlpResult;
    }
}