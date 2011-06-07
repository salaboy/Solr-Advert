#  Copyright 2011 Plugtree LLC
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

[when]Or=or

[when]Query has term "{term}" in field "{field}" = $q : AdvertQuery(); TermQuery(term.field=="{field}", term.text=="{term}")
[when]Any query = $q : AdvertQuery()

[then]Add boost query "{q}" = $q.boost("{q}");
[then]Set sort "{sortspec}" = $q.setSort("{sortspec}");
