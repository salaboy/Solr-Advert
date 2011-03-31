[when]Query has term "{term}" in field "{field}" = $q : AdvertQuery(eval($q.hasTerm("{field}", "{term}")))
[then]Add boost query "{q}" = $q.boost("{q}");
[when]Or=or