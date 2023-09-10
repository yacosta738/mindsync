import { expect, test } from '@playwright/test';

test('tests internationalization landing page', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.locator('#theme-toggle').click();
  await expect(
    page.getByRole('heading', {
      name: 'Second Brain, notes, and knowledge management for power users.',
    })
  ).toBeVisible();
  await expect(
    page.getByRole('heading', {
      name: "With MindSync, you'll be able to bring your ideas to life in record time.",
    })
  ).toBeVisible();
  await expect(
    page.getByRole('heading', {
      name: 'How MindSync helps you on a daily basis',
    })
  ).toBeVisible();
  await expect(page.getByText('Review notes')).toBeVisible();
  await expect(
    page.getByText(
      'Capture, structure, and review all of your class notes with ease using the Linke'
    )
  ).toBeVisible();
  await expect(page.getByText('Manage sources')).toBeVisible();
  await expect(
    page.getByText(
      'Keep track of your research and easily manage your sources using the built-in Zo'
    )
  ).toBeVisible();
  await expect(page.getByText('Investigate claims')).toBeVisible();
  await expect(
    page.getByText(
      'Spin your web of knowledge and see what evidence supports or contradicts claims.'
    )
  ).toBeVisible();
  await expect(page.getByText('Outline papers')).toBeVisible();
  await expect(
    page.getByText(
      'Manage your writing process and ensure that your papers are well-organized and f'
    )
  ).toBeVisible();
  await expect(
    page.getByRole('heading', { name: 'Work fast from anywhere' })
  ).toBeVisible();
  await expect(
    page.getByText(
      'Stay up to date and move work forward with Mindsync on iOS & Android. Download t'
    )
  ).toBeVisible();
  await expect(
    page.getByRole('link', { name: 'Download on the App Store' })
  ).toBeVisible();
  await expect(
    page.getByRole('link', { name: 'Get in on Google Play' })
  ).toBeVisible();
  await page.getByRole('combobox').selectOption('/es');
  await expect(
    page.getByRole('heading', {
      name: 'Segundo cerebro, notas y gestión del conocimiento para usuarios avanzados.',
    })
  ).toBeVisible();
  await expect(
    page.getByRole('heading', {
      name: 'Con MindSync, podrás dar vida a tus ideas en tiempo récord.',
    })
  ).toBeVisible();
  await expect(
    page.getByRole('heading', {
      name: 'Cómo MindSync te ayuda en tu día a día',
    })
  ).toBeVisible();
  await expect(page.getByText('Revisar notas')).toBeVisible();
  await expect(
    page.getByText(
      'Capture, estructure y revise todas sus notas de clase con facilidad utilizando l'
    )
  ).toBeVisible();
  await expect(page.getByText('Gestionar fuentes')).toBeVisible();
  await expect(
    page.getByText(
      'Haga un seguimiento de su investigación y gestione fácilmente sus fuentes utiliz'
    )
  ).toBeVisible();
  await expect(page.getByText('Investigar afirmaciones')).toBeVisible();
  await expect(
    page.getByText(
      'Teje tu red de conocimiento y observa qué evidencia respalda o contradice las af'
    )
  ).toBeVisible();
  await expect(page.getByText('Esquematizar trabajos')).toBeVisible();
  await expect(
    page.getByText(
      'Administra tu proceso de escritura y asegúrate de que tus trabajos estén bien or'
    )
  ).toBeVisible();
  await expect(
    page.getByRole('heading', { name: 'Trabaja rápido desde cualquier lugar' })
  ).toBeVisible();
  await expect(
    page.getByText(
      'Mantente actualizado y avanza en tu trabajo con Mindsync en iOS y Android. Desca'
    )
  ).toBeVisible();
  await expect(
    page.getByRole('link', { name: 'Descargar en App Store' })
  ).toBeVisible();
  await expect(
    page.getByRole('link', { name: 'Obtener en Google Play' })
  ).toBeVisible();
});
