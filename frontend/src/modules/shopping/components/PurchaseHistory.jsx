import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { FormattedDate, FormattedTime, FormattedNumber } from 'react-intl';
import backend from '../../../backend';
import { BackLink, Errors } from '../../common';
import shopping from '../../shopping';

const PurchaseHistory = () => {

    const dispatch = useDispatch();
    const purchaseSearch = useSelector(shopping.selectors.getPurchaseSearch);

    const purchases = purchaseSearch ? purchaseSearch.items : [];
    const existMore = purchaseSearch ? purchaseSearch.existMoreItems : false;

    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        return () => dispatch(shopping.actions.clearPurchaseSearch());
    }, [dispatch]);

    const loadPurchases = async (pageToLoad) => {
        setLoading(true);
        setError(null);
        const response = await backend.shoppingService.getPurchaseHistory(pageToLoad);
        setLoading(false);
        if (response.ok) {
            dispatch(shopping.actions.findPurchasesCompleted(response.payload));
        } else {
            setError(response.payload);
        }
    };

    useEffect(() => {
        loadPurchases(0);
    }, []);

    const handlePageChange = (newPage) => {
        setPage(newPage);
        loadPurchases(newPage);
    };

    if (loading && purchases.length === 0) {
        return <div className="text-center mt-5">Cargando historial...</div>;
    }

    if (error) {
        return (
            <div>
                <BackLink />
                <div className="mt-3">
                    <Errors errors={error} onClose={() => setError(null)} />
                </div>
            </div>
        );
    }

    return (
        <div>
            <BackLink />
            <h2 className="mt-3 mb-4">Historial de compras</h2>

            {purchases.length === 0 && !loading && (
                <div className="alert alert-info">No has realizado ninguna compra todavía.</div>
            )}

            {purchases.length > 0 && (
                <>
                    <div className="list-group">
                        {purchases.map(purchase => (
                            <div key={purchase.id} className="list-group-item mb-3 shadow-sm">
                                <div className="row">
                                    <div className="col-md-6">
                                        <h5><strong>Película:</strong> {purchase.movieTitle}</h5>
                                        <p><strong>ID Compra:</strong> {purchase.id}</p>
                                        <p><strong>Entradas:</strong> {purchase.tickets}</p>
                                        <p><strong>Precio total:</strong> <FormattedNumber value={purchase.totalPrice} style="currency" currency="EUR" /></p>
                                    </div>
                                    <div className="col-md-6">
                                        <p>
                                            <strong>Fecha compra:</strong> <FormattedDate value={new Date(purchase.date)} /> a las{' '}
                                            <FormattedTime value={new Date(purchase.date)} />
                                        </p>
                                        <p>
                                            <strong>Sesión:</strong> <FormattedDate value={new Date(purchase.sessionDate)} /> a las{' '}
                                            <FormattedTime value={new Date(purchase.sessionDate)} />
                                        </p>
                                        <p>
                                            <strong>Entregado:</strong>{' '}
                                            {purchase.delivered ? 'Sí' : 'No'}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="d-flex justify-content-between mt-4">
                        <button
                            className="btn btn-secondary"
                            disabled={page === 0}
                            onClick={() => handlePageChange(page - 1)}
                        >
                            Anterior
                        </button>
                        <button
                            className="btn btn-secondary"
                            disabled={!existMore}
                            onClick={() => handlePageChange(page + 1)}
                        >
                            Siguiente
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default PurchaseHistory;