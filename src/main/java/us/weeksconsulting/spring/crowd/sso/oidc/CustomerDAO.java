package us.weeksconsulting.spring.crowd.sso.oidc;

import org.springframework.data.repository.CrudRepository;

public interface CustomerDAO extends CrudRepository<Customer, Long> {
}
