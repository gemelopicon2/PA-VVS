import * as actionTypes from './actionTypes';

export const buyCompleted = (purchaseId) => ({
    type: actionTypes.BUY_COMPLETED,
    purchaseId
});

export const findPurchasesCompleted = purchaseSearch => ({
    type: actionTypes.FIND_PURCHASES_COMPLETED,
    purchaseSearch
});

export const clearPurchaseSearch = () => ({
    type: actionTypes.CLEAR_PURCHASE_SEARCH
});