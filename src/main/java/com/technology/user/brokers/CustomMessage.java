package com.technology.user.brokers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomMessage {
    private String content;
    private String fileHash;
}
