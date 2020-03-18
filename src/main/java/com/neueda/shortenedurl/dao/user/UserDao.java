package com.neueda.shortenedurl.dao.user;

import com.neueda.shortenedurl.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<AppUser, Long> {
	AppUser save(AppUser user);

	AppUser findByEmail(String email);
}
