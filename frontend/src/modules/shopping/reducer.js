import { combineReducers } from 'redux';
import * as actionTypes from './actionTypes';

const initialState = {
    lastPurchaseId: null,
    purchaseSearch: null,
};

const lastPurchaseId = (state = initialState.lastPurchaseId, action) => {
    switch (action.type) {
        case actionTypes.BUY_COMPLETED:
            return action.purchaseId;
        default:
            return state;
    }
}

const purchaseSearch = (state = initialState.purchaseSearch, action) => {
    switch (action.type) {
        case actionTypes.FIND_PURCHASES_COMPLETED:
            return action.purchaseSearch;
        case actionTypes.CLEAR_PURCHASE_SEARCH:
            return initialState.purchaseSearch;
        default:
            return state;
    }
}

const reducer = combineReducers({
    lastPurchaseId,
    purchaseSearch
});

export default reducer;