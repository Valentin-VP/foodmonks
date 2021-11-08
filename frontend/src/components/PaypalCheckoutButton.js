import React from 'react';
import  ReactDOM  from 'react-dom';
import paypal from 'paypal-checkout'
import { Noti } from '../components/Notification'


const PaypalCheckoutButton = ({order}) => {
    const paypalConf = {
        currency: 'USD',
        env: 'sandbox',
        client: {
            sandbox: `${process.env.REACT_APP_PAYPAL_CLIENT_ID}`,
            production: '--'
        },
        style: {
            label: 'pay',
            size: 'medium',
            shape: 'pill',
            color: 'gold'
        }
    };
    
    const PayPalButton = paypal.Button.driver('react', {React, ReactDOM});
    
    const payment = (data, actions) => {
        const payment = {
            transactions: [
                {
                    amount: {
                        total: order.total,
                        currency: paypalConf.currency,
                    },
                    description: 'Compra en Test App',
                    custom: order.customer || '',
                    item_list: {
                        items: order.items
                    }

                }
            ],
            note_to_payer: 'Contactanos por cualquier consulta ',
        };
    
    return actions.payment.create({payment});
};

const onAuthorize = (data, actions) => {
    return actions.payment.execute()
    .then(response => {
        console.log(response.cart)
        Noti("El pago fue realizado satisfactoriamente.");
    })
    .catch(error => {
        console.log(error);
        alert('Ocurrio un error al procesar el pago');
    });
};

const onError = (error) => {
    console.log(error);
    alert('El pago no fue realizado, reintente');
};

const onCancel = (data,actions) => {
    alert('Pago no realizado, el usuario cancelo el proceso');
};

    return (
        <PayPalButton
            env={paypalConf.env}
            client= {paypalConf.client}
            payment= {(data,actions)=> payment (data,actions)}
            onAuthorize={(data,actions)=> onAuthorize(data,actions)}
            onCancel={(data,actions)=> onCancel(data,actions)}
            onError={(error) => onError(error)}
            style= {paypalConf.style}
            commit
            locale="es_UY"
        />
            
    );
};

export default PaypalCheckoutButton