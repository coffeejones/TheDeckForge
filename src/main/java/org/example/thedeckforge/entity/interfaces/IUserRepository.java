package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Authority;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository {
    Authority logUserIn(Authority userAuth);
    void createUserAuthority(Authority userAuth);
    void createUser(User user);
    User getUserFromAuth(Authority userAuth);
    Long getUserLoginId(Authority userAuth);
    Authority getAuthorityByEmail(String email);
    Long getUserId(User user);
    void addCardToCollection(User user, Card card);
}
