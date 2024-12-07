package org.example.eventsphere.service;

public class YouTubeUrlConverter {

    public static String convertToEmbeddedUrl(String normalUrl) {
        if (normalUrl == null || normalUrl.isEmpty()) {
            return null;
        }

        String videoId = null;
        if (normalUrl.contains("youtube.com/watch?v=")) {
            videoId = normalUrl.substring(normalUrl.indexOf("v=") + 2);
            int ampersandPosition = videoId.indexOf('&');
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition);
            }
        } else if (normalUrl.contains("youtu.be/")) {
            videoId = normalUrl.substring(normalUrl.indexOf("youtu.be/") + 9);
        }

        if (videoId != null) {
            return "https://www.youtube.com/embed/" + videoId;
        } else {
            return null;
        }
    }
}
