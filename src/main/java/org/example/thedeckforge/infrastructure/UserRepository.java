package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Authority;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.Roles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class UserRepository implements org.example.thedeckforge.entity.interfaces.IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //@Override
    public Authority logUserIn(Authority userAuth) {
        String sql = "SELECT * FROM Credentials WHERE Email = ?";

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Authority(
                        rs.getString("Email"),
                        rs.getString("PasswordHash"),
                        Roles.valueOf(rs.getString("UserRole"))
                ),
                userAuth.getUsername()
        );
    }


    @Override
    public void createUserAuthority(Authority userAuth) {
        String sql = "INSERT INTO credentials (Email, PasswordHash) VALUES (?, ?)";
        jdbcTemplate.update(sql, userAuth.getUsername(), userAuth.getPassword());
    }

    @Override
    public void createUser(User user) {
        String sql = "INSERT INTO Users (Name, Age, UserCredentialsId) VALUES (?, ?, ?)";
        long authorityId = getUserLoginId(user.getAuthority());
        jdbcTemplate.update(sql, user.getName(), user.getDateOfBirth(), authorityId);
    }

    @Override
    public User getUserFromAuth(Authority userAuth) {
        String sql = """
        SELECT u.UserId, u.Name, u.Age, c.Email, c.UserRole
        FROM Users u
        JOIN Credentials c ON u.UserCredentialsId = c.CredentialsId
        WHERE c.Email = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Authority auth = new Authority(
                    rs.getString("Email"),
                    "",
                    Roles.valueOf(rs.getString("UserRole"))
            );
            User u = new User(rs.getString("Name"), rs.getDate("Age").toLocalDate(), auth);
            u.setId(rs.getLong("UserId"));
            return u;
        }, userAuth.getUsername());
    }

    @Override
    public Long getUserLoginId(Authority userAuth) {
        String sql = "SELECT CredentialsId FROM Credentials WHERE email = ?";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> rs.getLong("CredentialsId"), userAuth.getUsername()
        );
    }
    @Override
    public Authority getAuthorityByEmail(String email){
        String sqlQuery = "SELECT * FROM Credentials WHERE Email = ?";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) ->
                new Authority(
                        rs.getString("Email"),
                        rs.getString("PasswordHash"),
                        Roles.valueOf((rs.getString("UserRole").toUpperCase()))
                ),email
        );
    }
    @Override
    public Long getUserId(User user){
        String sql = "SELECT UserId FROM Users LEFT JOIN Credentials ON Users.UserCredentialsId = Credentials.CredentialsId WHERE Email = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("UserId"), user.getAuthority().getUsername());
    }


    @Override
    public void addCardToCollection(User user, Card card){
        String sql = "INSERT INTO Collections (UserId,CardId) VALUES (?,?)";
        jdbcTemplate.update(sql, user.getId(), card.getId());
    }
}
