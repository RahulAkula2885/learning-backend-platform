package in.rahul.learning.mail.utils;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MailServiceUtil {

    public String replacePlaceholdersByPattern(String str, Map<String, String> placeholders) {
        return replacePlaceholdersByPattern(str, "(\\{[^}]+\\})", placeholders);
    }

    public String replacePlaceholdersByPattern(String str, String rx, Map<String, String> placeholders) {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(rx);
        Matcher m = p.matcher(str);

        if (placeholders != null) {
            while (m.find()) {
                String match = m.group(1);
                String repString = placeholders.get(match);
                if (repString != null) {
                    m.appendReplacement(sb, repString);
                } else {
                    m.appendReplacement(sb, "");
                }
            }
        }
        m.appendTail(sb);

        return sb.toString();
    }

    @Cacheable(value = "mailTemplatesCache", key = "#resource.path", sync = true)
    public String readMailTemplateFile(Resource resource) {

        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}