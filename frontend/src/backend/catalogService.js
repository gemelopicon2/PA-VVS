import {appFetch} from './appFetch';

export const getBillboard = (date) =>
    appFetch('GET', `/catalog/movies?date=${date}`);

export const findMovieById = (id) =>
    appFetch('GET', `/catalog/movies/${id}`);

export const findSessionById = (id) =>
    appFetch('GET', `/catalog/sessions/${id}`);

export const findMovieById = (id) =>
    appFetch('GET', `/catalog/movies/${id}`);

export const findSessionById = (id) =>
    appFetch('GET', `/catalog/sessions/${id}`);