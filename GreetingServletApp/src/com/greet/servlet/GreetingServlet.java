package com.greet.servlet;

import com.greet.model.Greeting;
import com.greet.model.User;
import com.greet.service.GreetingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class GreetingServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "C:/greeting-app-uploads/";

    private GreetingService greetingService;

    @Override
    public void init() throws ServletException {
        WebApplicationContext context =
                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.greetingService = context.getBean(GreetingService.class);

        File uploadFolder = new File(UPLOAD_DIR);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (path.startsWith("/uploads/")) {
            serveUploadedFile(path, response);
        } else if (path.equals("/greetings")) {
            List<Greeting> greetingsList = greetingService.getAllGreetings();
            request.setAttribute("greetings", greetingsList);
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } else if (path.equals("/greetings/new")) {
            request.setAttribute("title", "Create Greeting");
            request.setAttribute("actionUrl", "greetings/new");
            request.setAttribute("greeting", new Greeting());
            request.getRequestDispatcher("/WEB-INF/views/greeting-form.jsp").forward(request, response);
        } else if (path.equals("/greetings/edit")) {
            int id = Integer.parseInt(request.getParameter("id"));
            Greeting existing = greetingService.getGreetingById(id);

            if (existing != null) {
                request.setAttribute("title", "Edit Greeting");
                request.setAttribute("actionUrl", "greetings/edit?id=" + id);
                request.setAttribute("greeting", existing);
                request.getRequestDispatcher("/WEB-INF/views/greeting-form.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/greetings");
            }
        } else if (path.equals("/greetings/delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            greetingService.deleteGreeting(id);
            response.sendRedirect(request.getContextPath() + "/greetings");
        } else {
            response.sendRedirect(request.getContextPath() + "/greetings");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        if (path.equals("/greetings/new")) {
            Greeting newGreeting = new Greeting();
            newGreeting.setMessage(request.getParameter("message"));
            newGreeting.setImagePath(getUploadedImagePath(request));
            newGreeting.setCreatedById(currentUser.getId());
            greetingService.createGreeting(newGreeting);
            response.sendRedirect(request.getContextPath() + "/greetings");
        } else if (path.equals("/greetings/edit")) {
            int id = Integer.parseInt(request.getParameter("id"));
            String existingImagePath = request.getParameter("existingImagePath");
            String imagePath = existingImagePath;
            Part filePart = request.getPart("imageFile");

            if (filePart != null && filePart.getSize() > 0) {
                imagePath = saveUploadedFile(filePart);
                if (existingImagePath != null && !existingImagePath.isBlank()) {
                    deleteFile(existingImagePath);
                }
            }

            Greeting updatedGreeting = new Greeting();
            updatedGreeting.setId(id);
            updatedGreeting.setMessage(request.getParameter("message"));
            updatedGreeting.setImagePath(imagePath);
            greetingService.updateGreeting(updatedGreeting);
            response.sendRedirect(request.getContextPath() + "/greetings");
        } else {
            response.sendRedirect(request.getContextPath() + "/greetings");
        }
    }

    private String getUploadedImagePath(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("imageFile");
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        return saveUploadedFile(filePart);
    }

    private String saveUploadedFile(Part part) throws IOException {
        String submittedFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String fileExt = "";
        int extIndex = submittedFileName.lastIndexOf('.');
        if (extIndex > 0) {
            fileExt = submittedFileName.substring(extIndex);
        }

        String uniqueName = UUID.randomUUID() + fileExt;
        String fullPath = UPLOAD_DIR + uniqueName;
        part.write(fullPath);
        return "/uploads/" + uniqueName;
    }

    private void serveUploadedFile(String requestPath, HttpServletResponse response) throws IOException {
        String fileName = requestPath.replace("/uploads/", "");
        Path filePath = Paths.get(UPLOAD_DIR, fileName).normalize();

        if (!filePath.startsWith(Paths.get(UPLOAD_DIR).normalize()) || !Files.exists(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(filePath.getFileName().toString());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        Files.copy(filePath, response.getOutputStream());
    }

    private void deleteFile(String relativePath) {
        String fileName = relativePath.replace("/uploads/", "");
        File file = new File(UPLOAD_DIR + fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
