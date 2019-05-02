package de.bitsandbooks.examples.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
public class SleeplessController {

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/abc"
    )
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Void check() {
        log.info("Controller received self check signal");
        return null;
    }

}
