package com.momentum.habittracker.Repository;

import com.momentum.habittracker.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
