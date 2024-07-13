# 기본 이미지 설정
FROM httpd:latest

# mod_proxy와 mod_proxy_http 모듈 활성화
RUN sed -i '/#LoadModule proxy_module/s/^#//g' /usr/local/apache2/conf/httpd.conf
RUN sed -i '/#LoadModule proxy_http_module/s/^#//g' /usr/local/apache2/conf/httpd.conf
