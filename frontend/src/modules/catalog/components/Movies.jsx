import React from 'react';
import { FormattedTime } from 'react-intl';
import { Link } from 'react-router';

const Movies = ({ movies }) => {
    if (!movies || movies.length === 0) {
        return <div className="alert alert-info">No hay películas en cartelera para este día.</div>;
    }

    return (
        <div className="container mt-3">
            {movies.map(movie => (
                <div key={movie.id} className="card mb-3 shadow-sm">
                    <div className="card-body">

                        <h5 className="card-title">
                            <Link to={`/catalog/movie-details/${movie.id}`} className="text-decoration-none text-dark fw-bold">
                                {movie.title}
                            </Link>
                        </h5>

                        <div className="sessions d-flex flex-wrap mt-2">
                            {movie.sessions.map(session => (
                                <Link
                                    key={session.id}
                                    to={`/catalog/session-details/${session.id}`}
                                    className="badge bg-primary m-1 p-2 text-decoration-none"
                                >
                                    <FormattedTime value={new Date(session.date)} />
                                </Link>
                            ))}
                        </div>

                    </div>
                </div>
            ))}
        </div>
    );
};

export default Movies;