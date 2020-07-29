// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/my-image-servlet")
public class FormHandlerServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the message entered by the user.
        String message = request.getParameter("message");

        // Get the URL of the image that the user uploaded to Blobstore.
        String imageUrl = getUploadedFileUrl(request, "image").map(String::toString).orElse("");

        //Storing the image urls and messages in Datastore
        Entity imageEntity = new Entity("Image");
        imageEntity.setProperty("imageUrl", imageUrl);
        imageEntity.setProperty("message", message);
        imageEntity.setProperty("timestamp", System.currentTimeMillis());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(imageEntity);
        response.sendRedirect("/index.html#upload-images");
    }

    /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
    private Optional<String> getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get(formInputElementName);

        // User submitted form without selecting a file, so we can't get a URL. (dev server)
        if (blobKeys == null || blobKeys.isEmpty()) {
            return Optional.empty();
        }

        // In the index.html form only contains a single file input, so get the first index.
        BlobKey blobKey = blobKeys.get(0);

        // User submitted form without selecting a file, so we can't get a URL. (live server)
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
        if (blobInfo.getSize() == 0) {
            blobstoreService.delete(blobKey);
            return Optional.empty();
        }

        // Check the validity of the file here, e.g. to make sure it's an image file
        // https://stackoverflow.com/q/10779564/873165

        // Use ImagesService to get a URL that points to the uploaded file.
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

        // To support running in Google Cloud Shell with AppEngine's dev server, we must use the relative
        // path to the image, rather than the path returned by imagesService which contains a host.
        try {
            URL url = new URL(imagesService.getServingUrl(options));
            return Optional.ofNullable(url.getPath());
        } catch (MalformedURLException e) {
            return Optional.ofNullable(imagesService.getServingUrl(options));
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Image").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<ImageAndMessage> imageDetails = new ArrayList<>();

        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String message = (String) entity.getProperty("message");
            String imageUrl = (String) entity.getProperty("imageUrl");
            long timestamp = (long) entity.getProperty("timestamp");
            ImageAndMessage image = new ImageAndMessage(id, message, imageUrl, timestamp);
            imageDetails.add(image);
        }

        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(imageDetails));
    }
}