package es.udc.paproject.backend.model.services;

import java.util.Optional;

import es.udc.paproject.backend.model.exceptions.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;

@Service
@Transactional(readOnly=true)
public class PermissionCheckerImpl implements PermissionChecker {
	
	@Autowired
	private UserDao userDao;

	@Override
	public User checkUser(Long userId) throws InstanceNotFoundException {

		Optional<User> user = userDao.findById(userId);
		
		if (!user.isPresent()) {
			throw new InstanceNotFoundException("project.entities.user", userId);
		}
		
		return user.get();
		
	}

    @Override
    public User checkIsUserViewer(Long userId) throws InstanceNotFoundException, PermissionException {
        return null;
    }

    @Override
    public User checkIsUserSeller(Long userId) throws InstanceNotFoundException, PermissionException {
        return null;
    }

    @Override
    public User checkPurchaseOwnership(Long userId, Long purchaseId) throws InstanceNotFoundException, PermissionException {
        return null;
    }
}
