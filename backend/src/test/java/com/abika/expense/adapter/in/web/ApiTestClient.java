package com.abika.expense.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Lets controller tests read as HTTP calls made by one logged-in user. */
record ApiTestClient(MockMvc mvc, String token) {

    static ApiTestClient registerNew(MockMvc mvc, String email) throws Exception {
        String body = mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"rahasia123","name":"Tester"}""".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return new ApiTestClient(mvc, JsonPath.read(body, "$.token"));
    }

    ResultActions get(String url) throws Exception {
        return send(MockMvcRequestBuilders.get(url));
    }

    ResultActions post(String url, String json) throws Exception {
        return send(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    ResultActions put(String url, String json) throws Exception {
        return send(MockMvcRequestBuilders.put(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    ResultActions delete(String url) throws Exception {
        return send(MockMvcRequestBuilders.delete(url));
    }

    static Long idFrom(ResultActions result) throws Exception {
        Number id = JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private ResultActions send(MockHttpServletRequestBuilder request) throws Exception {
        return mvc.perform(request.header("Authorization", "Bearer " + token));
    }
}
