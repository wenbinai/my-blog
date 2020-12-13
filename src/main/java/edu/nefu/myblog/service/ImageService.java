package edu.nefu.myblog.service;

import edu.nefu.myblog.response.ResponseResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Service
public interface ImageService {

    ResponseResult upload(MultipartFile file);

    void viewImage(HttpServletResponse response, String imageId);
}
