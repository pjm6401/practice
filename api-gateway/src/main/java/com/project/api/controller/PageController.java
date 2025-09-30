package com.project.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    /**
     * /wait 경로로 GET 요청이 오면 waiting.mustache 템플릿을 렌더링합니다.
     * @param ticket 인터셉터에서 리디렉션 시 전달한 티켓
     * @param model  View에 데이터를 전달하기 위한 객체
     * @return 템플릿 파일의 논리적 이름 ("waiting")
     */
    @GetMapping("/wait")
    public String waitingPage(@RequestParam String ticket, Model model) {
        // Mustache 템플릿에 ticket 값을 전달합니다.
        // 템플릿 안에서 {{ticket}} 형태로 사용할 수 있습니다.
        model.addAttribute("ticket", ticket);
        return "waiting"; // src/main/resources/templates/waiting.mustache 파일을 찾습니다.
    }
}
