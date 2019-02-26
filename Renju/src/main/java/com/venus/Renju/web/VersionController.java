/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.web;

import org.springframework.web.bind.annotation.*;
import java.util.*;
/**
 *
 * @author happy
 */
@CrossOrigin(origins="*")
@RestController
public class VersionController {
    
    /**
     * version
     * @return 
     */
    @RequestMapping(value="/renju/version")
    public Map<String,String> version(){
        LogCat.log(VersionController.class, "request version");
        Map<String,String> version_map=new HashMap<>();
        version_map.put("name", "Renju");
        version_map.put("version", "1.6");
        version_map.put("since", "2019.2.1");
        version_map.put("author", "yuwei, Dora");
        
        return version_map;
    }
    
}
