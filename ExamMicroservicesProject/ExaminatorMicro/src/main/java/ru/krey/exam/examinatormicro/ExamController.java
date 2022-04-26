package ru.krey.exam.examinatormicro;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExamController {

    private final RestTemplate restTemplate;

    private final DiscoveryClient discoveryClient;

    @PostMapping("/exam")
    public Exam getExam(@RequestBody Map<String,Integer> spec){
        List<Section> sections= spec.entrySet().stream()
                .map(this::getUrl)
                .map(url->restTemplate.getForObject(url,Question[].class))
                .map(Arrays::asList)
                .map(Section::new)
                .collect(Collectors.toList());
        return Exam.builder().title("exam1").sections(sections).build();
    }

    public String getUrl(Map.Entry<String,Integer> name){
        return "http://"+name.getKey()+"/api/questions?amount="+name.getValue();
    }
}
