import { useParams, Link } from 'react-router';

const PurchaseCompleted = () => {
    const { id } = useParams();

    return (
        <div className="container mt-5 text-center">
            <div className="alert alert-success p-5">
                <h2 className="mb-4">¡Compra realizada con éxito!</h2>
                <p className="fs-4">
                    Tu localizador de compra es: <strong>{id}</strong>
                </p>
                <p className="mt-4">
                    Por favor, presenta este identificador y tu tarjeta bancaria en taquilla para recoger tus entradas.
                </p>
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