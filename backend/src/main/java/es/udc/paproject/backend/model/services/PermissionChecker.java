package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.PermissionException;

public interface PermissionChecker {
	
	User checkUser(Long userId) throws InstanceNotFoundException;
	User checkIsUserViewer(Long userId) throws InstanceNotFoundException, PermissionException;
    User checkIsUserSeller(Long userId) throws InstanceNotFoundException, PermissionException;
    User checkPurchaseOwnership(Long userId, Long purchaseId) throws InstanceNotFoundException, PermissionException;
}
