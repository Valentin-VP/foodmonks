import React from 'react';
import {Container, InputGroup} from 'react-bootstrap'
import styled from 'styled-components';
import food from '../assets/food2.jpg'

const Styles = styled.div`
    .portada {
        display: flex;
        background-image: url(${food}) ;
        background-size: cover;
        background-color: #E5E5E5;
        height: 700px;
        justify-content: center;
        align-items: center;
        margin-bottom: 40px;
        }


        #barra{
            border: none;
            border-radius: 30px;
            margin-right: 15px;
            &:focus{
                box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
            }
        }
        
        #boton{
            width: 150px;
            color: white;
            background-color: #E87121;
            border: none;
            margin-left: 10px;
            border-radius: 30px;
            &:focus{
                box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
            }
        }
    }

    #grupo{
        border-radius: 5px;
        height: 50px;
    }

    #buscador{
    }
`;

export const Portada = () => (
    <Styles>
        <Container fluid className="portada">
            <Container id="buscador">
                <InputGroup id="grupo">
                    <input id="barra" type="text" className="form-control" placeholder="Dirección"/>
                    <button id="boton" type="submit" className="btn btn-secondary">Buscar</button>
                </InputGroup>
            </Container>
        </Container>
    </Styles>
  )