package models

case class User(username: String,
                role: String)

case class AuthUser(username: String,
                    password: String,
                    role: String)

case class UserPostData(username: String,
                        password: String)