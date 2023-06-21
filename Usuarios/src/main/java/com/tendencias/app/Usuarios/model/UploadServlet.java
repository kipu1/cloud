/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tendencias.app.Usuarios.model;

import com.google.cloud.storage.*;
import io.grpc.Context;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@WebServlet(name = "upload", value = "/upload")
@MultipartConfig()

public class UploadServlet extends HttpServlet {
    private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private static Storage storage = null;
    @Override
    public void init() {
        storage = StorageOptions.getDefaultInstance().getService();
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        final Part filePart = req.getPart("file");
        final String fileName = filePart.getSubmittedFileName();
// Modify access list to allow all users with link to read file
        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
// the inputstream is closed by default, so we don't need to close it here
        Blob blob = storage.create(
                BlobInfo.newBuilder(BUCKET_NAME, fileName).setAcl(acls).build(),
                filePart.getInputStream());
// return the public download link
        resp.getWriter().print(blob.getMediaLink());
    }
}