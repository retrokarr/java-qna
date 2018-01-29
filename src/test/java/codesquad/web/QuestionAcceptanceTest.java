package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static testhelper.HtmlFormDataBuilder.urlEncodedForm;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;


public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Test
    public void accessFormWithoutLogin() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void accessFormWithLogin() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/form", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void postTest() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request;
        request = urlEncodedForm().addParameter("title", "test")
                .addParameter("contents", "contests")
                .build();
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions/"));
    }

    @Test
    public void postTest_invalid_input() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request;
        request = urlEncodedForm().addParameter("title", "")
                .addParameter("contents", "contests")
                .build();
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/qna/form"));
    }

    @Test
    public void getPostTest() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/1", String.class);

        assertTrue(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));
    }

    @Test
    public void getPostTest_invalid_postNo() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/100", String.class);

        assertTrue(response.getBody().contains("질문하기"));
    }
}
