const getModuleState = state => state.catalog;

export const getMovies = state => getModuleState(state).movies;

export const getBillboardDate = state => getModuleState(state).billboardDate;