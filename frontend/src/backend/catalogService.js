import {appFetch} from './appFetch';

export const getBillboard = (date, onSuccess, onErrors) => {
    appFetch(`/catalog/billboard?date=${date}`, 'GET', null, onSuccess, onErrors);
};