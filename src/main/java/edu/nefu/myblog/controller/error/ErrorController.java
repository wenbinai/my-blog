package edu.nefu.myblog.controller.error;

import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.response.ResponseState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorController {

    @RequestMapping(value = "/403", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @ResponseBody
    public ResponseResult page403() {
        ResponseResult responseResult = new ResponseResult(ResponseState.PERMISSION_FORBID);
        return responseResult;
    }
}
