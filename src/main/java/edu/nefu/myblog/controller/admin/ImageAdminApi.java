package edu.nefu.myblog.controller.admin;

import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {
    @Autowired
    private ImageService imageService;

    @PreAuthorize("@permission.adminPermission()")
    @PostMapping()
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile file,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        return imageService.upload(file, request, response);
    }

    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/{imageId}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("imageId") String imageId) {
        imageService.viewImage(response, imageId);
    }

    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId) {
        return imageService.deleteById(imageId);
    }

    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("list/{page}/{size}")
    public ResponseResult listImages(@PathVariable("page") int page,
                                     @PathVariable("size") int size) {
        return imageService.listImages(page, size);
    }

}
