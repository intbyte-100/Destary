const API = 'http://localhost:8080';
const $ = selector => document.querySelector(selector);
let access = localStorage.getItem('destary_access_token') || '';
let refresh = localStorage.getItem('destary_refresh_token') || '';
let userId = localStorage.getItem('destary_user_id') || '';
let profile = null;
let timer;
$('#date').textContent = new Intl.DateTimeFormat('ru', {day:'numeric', month:'long', weekday:'long'}).format(new Date());
function notify(message) { const toast = $('#toast'); toast.textContent = message; toast.classList.add('show'); clearTimeout(timer); timer = setTimeout(() => toast.classList.remove('show'), 5000); }
function saveTokens(data) { access = data.accessToken; refresh = data.refreshToken; userId = String(data.userId); localStorage.setItem('destary_access_token', access); localStorage.setItem('destary_refresh_token', refresh); localStorage.setItem('destary_user_id', userId); }
function logout() { access = ''; refresh = ''; userId = ''; profile = null; ['destary_access_token','destary_refresh_token','destary_user_id'].forEach(key => localStorage.removeItem(key)); renderSession(); $('#posts').innerHTML = '<div class="empty card"><div class="icon">✳</div><h3>Пока здесь тихо</h3><p>Войдите и поделитесь первой мыслью.</p></div>'; }
async function request(path, options = {}, retry = true) {
  const headers = {...(options.body ? {'Content-Type':'application/json'} : {}), ...(options.auth !== false && access ? {Authorization:'Bearer ' + access} : {})};
  let response; try { response = await fetch(API + path, {method:options.method || 'GET', headers, ...(options.body ? {body:JSON.stringify(options.body)} : {})}); } catch { throw new Error('Нет соединения с сервером. Проверьте localhost:8080 и настройки CORS.'); }
  if (response.status === 401 && retry && options.auth !== false && refresh) { try { saveTokens(await request('/api/auth/refresh', {method:'POST',body:{refreshToken:refresh},auth:false}, false)); return request(path, options, false); } catch { logout(); throw new Error('Сессия завершилась. Войдите снова.'); } }
  const text = await response.text(); let data; try { data = text ? JSON.parse(text) : {}; } catch { data = {}; } if (!response.ok) throw new Error(data.detail || data.message || 'Ошибка сервера: ' + response.status); return data;
}
function renderSession() { const loggedIn = Boolean(access); $('#auth').classList.toggle('hidden',loggedIn); $('#composer').classList.toggle('hidden',!loggedIn); $('#logout').classList.toggle('hidden',!loggedIn); $('#profileName').textContent = profile?.username || 'Ваш профиль'; $('#profileEmail').textContent = profile?.email || 'Войдите, чтобы быть на связи'; document.querySelectorAll('.my-avatar').forEach(el => el.textContent = profile?.username?.[0]?.toUpperCase() || 'D'); }
function renderPost(post) { $('#posts .empty')?.remove(); const article = document.createElement('article'); article.className = 'post card'; const head = document.createElement('div'); head.className = 'post-head'; const avatar = document.createElement('div'); avatar.className = 'avatar'; const name = post.userId == userId && profile ? profile.username : 'Пользователь #' + post.userId; avatar.textContent = name[0].toUpperCase(); const info = document.createElement('div'); const author = document.createElement('b'); author.textContent = name; const time = document.createElement('small'); const date = new Date(post.createdAt); time.textContent = Number.isNaN(date.getTime()) ? '' : new Intl.DateTimeFormat('ru',{dateStyle:'long',timeStyle:'short'}).format(date); info.append(author,time); head.append(avatar,info); const content = document.createElement('p'); content.className = 'post-content'; content.textContent = post.content; const foot = document.createElement('div'); foot.className = 'post-foot'; foot.textContent = 'Публикация #' + post.id + ' · Destary'; article.append(head,content,foot); $('#posts').prepend(article); }
async function loadMyPosts() {
  if (!userId) { notify('Войдите снова, чтобы загрузить публикации.'); return; }
  const currentUser = userId;
  const count = await request('/api/users/' + encodeURIComponent(currentUser) + '/posts/count');
  const posts = [];
  for (let index = 0; index < count; index++) {
    posts.push(await request('/api/users/' + encodeURIComponent(currentUser) + '/posts?index=' + index));
  }
  if (!access || userId !== currentUser) return;
  $('#posts').replaceChildren();
  if (posts.length) posts.reverse().forEach(renderPost);
  else $('#posts').innerHTML = '<div class="empty card"><div class="icon">✳</div><h3>Пока здесь тихо</h3><p>Поделитесь первой мыслью.</p></div>';
}
async function loadProfile() { profile = await request('/api/user/me'); renderSession(); await loadMyPosts(); }
document.querySelectorAll('.tab').forEach(tab => tab.addEventListener('click', () => { document.querySelectorAll('.tab').forEach(item => item.classList.toggle('selected',item === tab)); $('#loginForm').classList.toggle('hidden',tab.dataset.tab !== 'login'); $('#registerForm').classList.toggle('hidden',tab.dataset.tab !== 'register'); }));
for (const [selector,path] of [['#loginForm','/api/auth/login'],['#registerForm','/api/auth/register']]) $(selector).addEventListener('submit', async event => { event.preventDefault(); try { const data = await request(path,{method:'POST',auth:false,body:Object.fromEntries(new FormData(event.currentTarget))}); saveTokens(data); renderSession(); await loadProfile(); notify('Добро пожаловать в Destary!'); } catch (error) { notify(error.message); } });
$('#postForm textarea').addEventListener('input',event => $('#count').textContent = event.target.value.length + ' / 64');
$('#postForm').addEventListener('submit',async event => { event.preventDefault(); const form = event.currentTarget; try { const post = await request('/api/post',{method:'POST',body:{content:new FormData(form).get('content')}}); renderPost(post); form.reset(); $('#count').textContent = '0 / 64'; notify('Публикация добавлена'); } catch (error) { notify(error.message); } });
$('#findForm').addEventListener('submit',async event => { event.preventDefault(); try { renderPost(await request('/api/post/' + encodeURIComponent(new FormData(event.currentTarget).get('postId')))); } catch (error) { notify(error.message); } });
$('#logout').addEventListener('click',logout);
renderSession(); if (access) loadProfile().catch(error => notify(error.message));
