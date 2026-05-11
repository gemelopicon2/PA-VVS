const getModuleState = state => state.shopping;

export const getLastPurchaseId = state =>
    getModuleState(state).lastPurchaseId;

export const getPurchaseSearch = state =>
    getModuleState(state).purchaseSearch;