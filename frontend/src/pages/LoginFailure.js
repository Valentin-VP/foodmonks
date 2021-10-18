import React from "react";
import { Alert } from "react-bootstrap";

export const LoginFailure = ({error}) => (
        <Alert variant="danger">
            {error}
        </Alert>
);