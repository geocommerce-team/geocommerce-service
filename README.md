# geocommerce-service

------------------------

## Описание
Java Spring Boot сервис зон рекомендаций для открытия новой торговой точки, учитывает плотность населения, рядом находящиеся торговые точки, точки с доступной арендой.

Использует следующие сервисы:
1. https://github.com/geocommerce-team/geocommerce-rentpoints-service - сервис точек аренды.
2. https://github.com/geocommerce-team/geocommerce-retailpoints-service - сервис торговых точек.
3. https://github.com/geocommerce-team/geocommerce-population-service - сервис плотности населения.
4. Nominatim API.

## End Point
Принимает `GET` запрос по `URI`: <pre>/geocommerce/api/recommendations?category={category}&latMin={latMin}&lonMin={lonMin}&latMax={latMax}&lonMax={lonMax}</pre>

Параметр `category` - строка, доступные варианты получать из сервиса торговых точек.

