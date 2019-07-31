package com.example.poc.service.domain;

import lombok.*;

/**
 * @author Taylan Erden
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private String id;

  private String fullName;

  private String userName;

  private String gsmNo;

  private String email;

  private String password;
}
