import React,{useState} from 'react';
import { Button, Container } from 'react-bootstrap';
import { useDispatch } from 'react-redux';
import styled from 'styled-components';
import {fetchUserData} from '../../api/authenticationService';


const MainWrapper=styled.div`
    padding-top:40px;
`;

export const Principal=(props)=>{

    const dispatch=useDispatch();
    const [loading,setLoading]=useState(false);
    const [data,setData]=useState({});

    React.useEffect(()=>{
        fetchUserData().then((response)=>{
            setData(response.data);
        }).catch((e)=>{
            localStorage.clear();
            props.history.push('/');
        })
    },[])

    const logOut=()=>{

        localStorage.clear();
        props.history.push('/');

    }
{/*ojo con los cambios en 'data'*/}
    return (
        <Container>
            <MainWrapper>
                <h4>Hello {data && `${data.firstName} ${data.lastName}`}</h4>{/*falta imprimir los usuarios(arreglar)*/}
                <br></br>
                {data && data.roles && data.roles.filter(value => value.authority==='ROLE_ADMIN').length>0 && <Button type="variant" onClick={() =>{console.log("soy admin XD")}}>Admin</Button>}
                <br></br>
                <Button style={{marginTop:'5px'}} onClick={() =>logOut()}>Logout</Button>
            </MainWrapper>
        </Container>
    )
}