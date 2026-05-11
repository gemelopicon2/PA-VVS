import { useParams, Link } from 'react-router';
import { useSelector } from 'react-redux';
import shopping from '../../shopping';

const PurchaseCompleted = () => {
    const { id: urlId } = useParams();

    const reduxId = useSelector(shopping.selectors.getLastPurchaseId);

    const purchaseId = reduxId || urlId;

    return (
        <div className="container mt-5 text-center">
            <div className="alert alert-success p-5">
                <h2 className="mb-4">¡Compra realizada con éxito!</h2>

                {purchaseId ? (
                    <>
                        <p className="fs-4">
                            Tu localizador de compra es: <strong>{purchaseId}</strong>
                        </p>
                        <p className="mt-4">
                            Por favor, presenta este identificador y tu tarjeta bancaria en taquilla para recoger tus entradas.
                        </p>
                    </>
                ) : (
                    <p className="fs-4 text-danger">
                        No se ha podido recuperar el identificador de la compra.
                    </p>
                )}

                <div className="mt-5">
                    <Link to="/" className="btn btn-primary">
                        Volver a la cartelera
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default PurchaseCompleted;