const getModuleState = state => state.catalog;

export const getMovies = state => getModuleState(state).movies;