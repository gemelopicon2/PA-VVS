import {appFetch} from './appFetch';

export const getBillboard = (date) =>
    appFetch('GET', `/catalog/movies?date=${date}`);