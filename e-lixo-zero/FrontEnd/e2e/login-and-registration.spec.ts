import { expect, test, type Page } from '@playwright/test';

type ApiScenario = {
  login?: 'success' | 'invalid';
  registration?: 'success' | 'duplicate';
};

async function useApiScenario(page: Page, scenario: ApiScenario = {}) {
  await page.route('**/api/**', async (route) => {
    const request = route.request();
    const url = new URL(request.url());

    if (request.method() === 'POST' && url.pathname === '/api/users/login') {
      if (scenario.login === 'invalid') {
        await route.fulfill({
          status: 401,
          contentType: 'application/json',
          body: JSON.stringify({ message: 'Invalid email or password' }),
        });
        return;
      }

      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          id: 1,
          fullName: 'João Silva',
          email: 'joao@gmail.com',
          userType: 'CITIZEN',
          token: 'e2e-token',
        }),
      });
      return;
    }

    if (request.method() === 'POST' && url.pathname === '/api/users') {
      if (scenario.registration === 'duplicate') {
        await route.fulfill({
          status: 400,
          contentType: 'application/json',
          body: JSON.stringify({ message: 'E-mail já cadastrado' }),
        });
        return;
      }

      await route.fulfill({ status: 201, body: '' });
      return;
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([]),
    });
  });
}

async function fillRegistration(page: Page, email = 'novo.usuario@elixozero.test') {
  await page.getByPlaceholder('Digite seu nome').fill('Novo Usuário E2E');
  await page.getByPlaceholder('seu@email.com').fill(email);
  await page.getByPlaceholder('Rua, Avenida...').fill('Rua dos Testes');
  await page.getByPlaceholder('123').fill('123');
  await page.getByPlaceholder('Centro').fill('Centro');
  await page.getByPlaceholder('Santa Rita do Sapucaí').fill('Santa Rita do Sapucaí');
  await page.getByPlaceholder('Crie uma senha').fill('SenhaForte123');
  await page.getByPlaceholder('Confirme sua senha').fill('SenhaForte123');
}

test('E2E-LOGIN-01: credenciais válidas abrem o dashboard', async ({ page }) => {
  await useApiScenario(page, { login: 'success' });
  await page.goto('/login');
  await page.getByLabel('E-mail').fill('joao@gmail.com');
  await page.getByLabel('Senha').fill('123456');

  const loginRequest = page.waitForRequest(
    (request) => request.method() === 'POST' && new URL(request.url()).pathname === '/api/users/login',
  );
  await page.getByRole('button', { name: 'Entrar' }).click();
  expect((await loginRequest).postDataJSON()).toEqual({
    email: 'joao@gmail.com',
    password: '123456',
  });

  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(page.getByRole('heading', { name: /Olá, João Silva/ })).toBeVisible();
  await expect.poll(() => page.evaluate(() => localStorage.getItem('token'))).toBe('e2e-token');
});

test('E2E-LOGIN-02: credenciais inválidas exibem mensagem de erro', async ({ page }) => {
  await useApiScenario(page, { login: 'invalid' });
  await page.goto('/login');
  await page.getByLabel('E-mail').fill('incorreto@elixozero.test');
  await page.getByLabel('Senha').fill('senha-incorreta');

  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page.getByText('E-mail ou senha inválidos.')).toBeVisible();
  await expect(page).toHaveURL(/\/login$/);
});

test('E2E-CAD-01: cadastro aceito retorna ao login com confirmação', async ({ page }) => {
  await useApiScenario(page, { registration: 'success' });
  await page.goto('/register');
  await fillRegistration(page);

  const registrationRequest = page.waitForRequest(
    (request) => request.method() === 'POST' && new URL(request.url()).pathname === '/api/users',
  );
  await page.getByRole('button', { name: 'Cadastrar' }).click();
  const request = await registrationRequest;

  expect(request.postDataJSON()).toMatchObject({
    fullName: 'Novo Usuário E2E',
    email: 'novo.usuario@elixozero.test',
    password: 'SenhaForte123',
    street: 'Rua dos Testes',
    number: '123',
    neighborhood: 'Centro',
    city: 'Santa Rita do Sapucaí',
    state: 'MG',
    phone: '',
    userType: 'CITIZEN',
    cpf: '',
  });
  await expect(page).toHaveURL(/\/login\?cadastro=sucesso$/);
  await expect(page.getByRole('status')).toHaveText('Cadastro realizado. Faça login para continuar.');
});

test('E2E-CAD-02: senhas diferentes impedem o envio do cadastro', async ({ page }) => {
  await useApiScenario(page, { registration: 'success' });
  let registrationRequests = 0;
  page.on('request', (request) => {
    if (request.method() === 'POST' && new URL(request.url()).pathname === '/api/users') {
      registrationRequests += 1;
    }
  });

  await page.goto('/register');
  await fillRegistration(page);
  await page.getByPlaceholder('Confirme sua senha').fill('senha-diferente');
  await page.getByRole('button', { name: 'Cadastrar' }).click();

  await expect(page.getByText('As senhas não coincidem.')).toBeVisible();
  expect(registrationRequests).toBe(0);
});

test('E2E-CAD-03: e-mail já cadastrado exibe falha', async ({ page }) => {
  await useApiScenario(page, { registration: 'duplicate' });
  await page.goto('/register');
  await fillRegistration(page, 'joao@gmail.com');
  await page.getByRole('button', { name: 'Cadastrar' }).click();

  await expect(page.getByText('Erro ao cadastrar usuário.')).toBeVisible();
  await expect(page).toHaveURL(/\/register$/);
});
