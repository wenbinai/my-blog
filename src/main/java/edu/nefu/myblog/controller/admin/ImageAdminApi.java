package edu.nefu.myblog.controller.admin;

import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {
    @Autowired
    private ImageService imageService;

    @PreAuthorize("@permission.adminPermission()")
    @PostMapping()
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile file) {
        return imageService.upload(file);
    }

    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/{imageId}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("imageId") String imageId) {
        imageService.viewImage(response, imageId);
    }
}
