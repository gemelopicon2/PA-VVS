import { useState, useEffect } from 'react';
import { useParams } from 'react-router';
import backend from '../../../backend';
import { BackLink } from '../../common';

const MovieDetails = () => {
    const [movie, setMovie] = useState(null);
    const { id } = useParams();
    const movieId = Number(id);

    useEffect(() => {
        const findMovieById = async movieId => {
            if (!Number.isNaN(movieId)) {
                const response = await backend.catalogService.findMovieById(movieId);
                if (response.ok) {
                    setMovie(response.payload);
                }
            }
        };
        findMovieById(movieId);
    }, [movieId]);

    if (!movie) return null;

    return (
        <div>
            <BackLink />
            <div className="card mt-3">
                <div className="card-body">
                    <h2 className="card-title">{movie.title}</h2>
                    <p className="card-text"><strong>Resumen:</strong> {movie.summary}</p>
                    <p className="card-text"><strong>Duración:</strong> {movie.duration} minutos</p>
                </div>
            </div>
        </div>
    );
};

export default MovieDetails;