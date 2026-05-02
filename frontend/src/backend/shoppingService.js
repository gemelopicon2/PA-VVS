import { appFetch } from './appFetch';

export const buy = async (sessionId, tickets, creditCard) => {
    return appFetch('POST', `/shopping/purchases`, {
        sessionId: sessionId,
        tickets: tickets,
        creditCard: creditCard
    });
};