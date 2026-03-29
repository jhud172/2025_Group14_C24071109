package uk.ac.cf._5.group14.One_To_One.Web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Thymeleaf layout fragments render the {@code <html>} element but omit the
 * document type declaration, which pushes pages into Quirks Mode. Inject a
 * standards-mode doctype centrally for full HTML documents that are missing it.
 */
@Component
public class HtmlDoctypeResponseFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/img/")
                || path.startsWith("/webjars/")
                || path.startsWith("/api/")
                || path.startsWith("/actuator/")
                || path.startsWith("/favicon")
                || path.endsWith(".ico")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".webp")
                || path.endsWith(".svg")
                || path.endsWith(".gif")
                || path.endsWith(".css")
                || path.endsWith(".js");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, cachingResponse);
            prependDoctypeIfNeeded(cachingResponse);
        } finally {
            cachingResponse.copyBodyToResponse();
        }
    }

    private void prependDoctypeIfNeeded(ContentCachingResponseWrapper response) throws IOException {
        byte[] body = response.getContentAsByteArray();
        if (!isHtmlDocumentMissingDoctype(response.getContentType(), body, response.getCharacterEncoding())) {
            return;
        }

        Charset charset = resolveCharset(response.getCharacterEncoding());
        byte[] doctype = "<!DOCTYPE html>\n".getBytes(charset);
        byte[] updated = new byte[doctype.length + body.length];
        System.arraycopy(doctype, 0, updated, 0, doctype.length);
        System.arraycopy(body, 0, updated, doctype.length, body.length);

        response.resetBuffer();
        response.setContentLengthLong(updated.length);
        response.getOutputStream().write(updated);
    }

    private boolean isHtmlDocumentMissingDoctype(String contentType,
                                                 byte[] body,
                                                 String encoding) {
        if (body == null || body.length == 0 || contentType == null) {
            return false;
        }

        String lowerContentType = contentType.toLowerCase(Locale.ROOT);
        if (!lowerContentType.contains(MediaType.TEXT_HTML_VALUE)) {
            return false;
        }

        Charset charset = resolveCharset(encoding);
        String markup = new String(body, charset).stripLeading();
        if (markup.isEmpty()) {
            return false;
        }

        String lowerMarkup = markup.toLowerCase(Locale.ROOT);
        if (lowerMarkup.startsWith("<!doctype html")) {
            return false;
        }

        return lowerMarkup.startsWith("<html");
    }

    private Charset resolveCharset(String encoding) {
        if (encoding == null || encoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception ex) {
            return StandardCharsets.UTF_8;
        }
    }
}
