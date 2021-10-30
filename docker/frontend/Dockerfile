# Etapa 1 - Contenedor para build
FROM node:16.10.0 as builder
ARG REACT_APP_BACKEND_URL_BASE=${REACT_APP_BACKEND_URL_BASE}
ARG REACT_APP_API_KEY=${REACT_APP_API_KEY}
ARG REACT_APP_AUTH_DOMAIN=${REACT_APP_AUTH_DOMAIN}
ARG REACT_APP_PROYECT_ID=${REACT_APP_PROYECT_ID}
ARG REACT_APP_STORAGE_BUCKET=${REACT_APP_STORAGE_BUCKET}
ARG REACT_APP_MESSAGING_SENDER_ID=${REACT_APP_MESSAGING_SENDER_ID}
ARG REACT_APP_APP_ID=${REACT_APP_APP_ID}
ARG REACT_APP_GENERIC_MENU=${REACT_APP_GENERIC_MENU}
ARG REACT_APP_GOOGLE_MAPS_API_KEY=${REACT_APP_GOOGLE_MAPS_API_KEY}
RUN mkdir /usr/src/app
WORKDIR /usr/src/app

COPY src /usr/src/app/src
COPY public /usr/src/app/public
COPY package.json /usr/src/app/package.json
COPY package-lock.json /usr/src/app/package-lock.json
RUN npm install
RUN REACT_APP_BACKEND_URL_BASE=http://localhost/ npm run build

# Etapa 2 - Contenedor para producción

FROM nginx:1.12-alpine as prod
COPY --from=builder /usr/src/app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]