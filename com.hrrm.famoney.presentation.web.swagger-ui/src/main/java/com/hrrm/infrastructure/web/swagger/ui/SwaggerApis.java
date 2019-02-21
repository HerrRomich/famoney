package com.hrrm.infrastructure.web.swagger.ui;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SwaggerApis {

    void writeApisJs(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void writeApiJson(String apiName, HttpServletRequest request, HttpServletResponse response);

}
